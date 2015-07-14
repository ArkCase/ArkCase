Acm.Model = {
    create: function() {
    }
    ,
    onInitialized: function() {
    }

    ,Variable: function(initValue) {
        this.value = initValue;
    }

    ,SessionData: function(name) {
        this.name = name;
    }

    ,LocalData: function(name) {
        this.name = name;
    }

    ,CacheFifo: function(arg) {
        this.maxSize = Acm.goodValue2([arg, "maxSize"], this.DEFAULT_MAX_CACHE_SIZE);
        this.reset();

        this.name = Acm.goodValue2([arg, "name"], "Cache" + Math.floor((Math.random()*1000000000)));
        this.expiration = Acm.goodValue2([arg, "expiration"] , this.DEFAULT_EXPIRATION);   //arg.expiration in milliseconds; -1 if never expired
        this.evict(this.name, this.expiration);

    }
}

Acm.Model.Variable.prototype = {
    get: function() {
        return this.value;
    }
    ,set: function(value) {
        this.value = value;
    }
}

//
//data stored in SessionStorage
//
Acm.Model.SessionData.prototype = {
    getName: function() {
        return this.name;
    }
    ,get: function() {
        var data = sessionStorage.getItem(this.name);
        var item = ("null" === data)? null : JSON.parse(data);
        return item;
    }
    ,set: function(data) {
        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
        sessionStorage.setItem(this.name, item);
    }
}

//
//data stored in LocalStorage
//
Acm.Model.LocalData.prototype = {
    getName: function() {
        return this.name;
    }
    ,get: function() {
        var data = localStorage.getItem(this.name);
        var item = ("null" === data)? null : JSON.parse(data);
        return item;
    }
    ,set: function(data) {
        var item = (Acm.isEmpty(data))? null : JSON.stringify(data);
        localStorage.setItem(this.name, item);
    }
}

//
// First in first out aging cache
//
// If a key is already exist, put() updates the value.
// An key can be locked, so that it has higher priority not to be aged first.
//
Acm.Model.CacheFifo.prototype = {
    DEFAULT_MAX_CACHE_SIZE: 8

    ,DEFAULT_EXPIRATION: 7200000           //2 hours = 2 * 3600 * 1000 milliseconds

    ,get: function(key) {
        for (var i = 0; i < this.size; i++) {
            if (this.keys[i] == key) {
                return this.cache[key];
            }
        }
        return null;
    }
    ,put: function(key, item) {
        var putAt = -1;
        for (var i = 0; i < this.size; i++) {
            if (this.keys[i] == key) {
                putAt = i;
                break;
            }
        }

        if (0 > putAt) {
            putAt = this._getNext();
            this._advanceToNext();
        }

        this.cache[key] = item;
        this.timeStamp[key] = new Date().getTime();
        this.keys[putAt] = key;
    }
    ,_getNext: function() {
        return this._getNextN(0);
    }
    //Use n to keep track number of recursive call to _getNextN(), so that it will not exceed maxSize and into an infinite loop
    ,_getNextN: function(n) {
        var next = this.next;
        if (!this.isLock(this.keys[next])) {
            return next;
        }

        if (n > this.maxSize) {     //when n == maxSize, _getNextN() is called maxSize times, pick the first one to avoid infinite loop
            return next;
        }

        this._advanceToNext();
        return this._getNextN(n + 1);

    }
    ,_advanceToNext: function() {
        this.next = (this.next + 1) % this.maxSize;
        this.size = (this.maxSize > this.size)? (this.size + 1) : this.maxSize;
    }
    ,remove: function(key) {
        var delAt = -1;
        for (var i = 0; i < this.size; i++) {
            if (this.keys[i] == key) {
                delAt = i;
                break;
            }
        }

        if (0 <= delAt) {
            var newKeys = [];
            for (var i = 0; i < this.maxSize; i++) {
                newKeys.push(null);
            }

            if (this.size == this.maxSize) {
                var n = 0;
                for (var i = 0; i < this.size; i++) {
                    if (i != delAt) {
                        newKeys[n] = this.keys[(this.next + i + this.maxSize) % this.maxSize];
                        n++;
                    }
                }
            } else {
                var n = 0;
                for (var i = 0; i < this.size; i++) {
                    if (i != delAt) {
                        newKeys[n] = this.keys[i];
                        n++;
                    }
                }
            }
            this.size--;
            this.next = this.size;

            this.keys = newKeys;
            delete this.cache[key];
            delete this.timeStamp[key];
        } //end if (0 <= delAt) {
    }
    ,reset: function() {
        this.next = 0;
        this.size = 0;
        this.cache = {};
        this.timeStamp = {};
        this.keys = [];
        for (var i = 0; i < this.maxSize; i++) {
            this.keys.push(null);
        }
        this.locks = [];
    }
    ,evict: function(name, expiration) {
        if (0 < expiration) {
            var that = this;
            Acm.Timer.useTimer(name
                ,300000     //every 5 minutes = 5 * 60 * 1000 milliseconds
                ,function() {
                    var keys = that.keys;
                    var len = keys.length;
                    for (var i = 0; i < that.size; i++) {
                        var key = keys[i];
                        var ts = that.timeStamp[key];
                        var now = new Date().getTime();
                        if (expiration < now - ts) {
                            that.remove(key);
                        }
                        var z = 1;
                    }
                    return true;
                }
            );
        }

    }
    ,lock: function(key) {
        this.locks.push(key);
    }
    ,unlock: function(key) {
        for (var i = 0; i < this.locks.length; i++) {
            if (this.locks[i] == key) {
                this.locks.splice(i, 1);
                return;
            }
        }
    }
    ,isLock: function(key) {
        for (var i = 0; i < this.locks.length; i++) {
           if (this.locks[i] == key) {
               return true;
           }
        }
        return false;
    }
    ,getMaxSize: function() {
        return this.maxSize;
    }
    ,setMaxSize: function(maxSize) {
        this.maxSize = maxSize;
    }
    ,getExpiration: function() {
        return this.expiration;
    }
    ,setExpiration: function(expiration) {
        this.expiration = expiration;
    }

};
