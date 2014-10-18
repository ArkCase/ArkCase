Acm.Model = {
    create: function() {
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
//simple first in first out aging cache
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
        var putAt = this.next;
        for (var i = 0; i < this.size; i++) {
            if (this.keys[i] == key) {
                putAt = i;
                break;
            }
        }


        this.cache[key] = item;
        this.keys[putAt] = key;


        if (putAt == this.next) {
            this.next = (this.next + 1) % this.maxSize;
            this.size = (this.maxSize > this.size)? (this.size + 1) : this.maxSize;
        }
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
    }
    ,getMaxSize: function() {
        return this.maxSize;
    }
    ,setMaxSize: function(maxSize) {
        this.maxSize = maxSize;
    }
};
