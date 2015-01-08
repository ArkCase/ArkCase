Acm.Model = {
    create: function() {
    }
    ,
    onInitialized: function() {
    }

    ,SessionData: function(name) {
        this.name = name;
    }

    ,CacheFifo: function(maxSize) {
        this.maxSize = maxSize;
        this.reset();
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
// First in first out aging cache
//
// If a key is already exist, put() updates the value.
// An key can be locked, so that it has higher priority not to be aged first.
//
Acm.Model.CacheFifo.prototype = {
    get: function(key) {
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
        } //end if (0 <= delAt) {
    }
    ,reset: function() {
        this.next = 0;
        this.size = 0;
        this.cache = {};
        this.keys = [];
        for (var i = 0; i < this.maxSize; i++) {
            this.keys.push(null);
        }
        this.locks = [];
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
};
