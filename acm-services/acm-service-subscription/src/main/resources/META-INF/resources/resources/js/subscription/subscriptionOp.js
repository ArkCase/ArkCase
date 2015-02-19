/**
 * SubscriptionOp is namespace component for Subscription Operation
 *
 * @author jwu
 */
var SubscriptionOp = SubscriptionOp || {
    create: function(args) {
        if (Acm.isEmpty(args)) {
            args = {};
        }

        if (SubscriptionOp.Controller.create) {SubscriptionOp.Controller.create(args);}
        if (SubscriptionOp.Model.create)      {SubscriptionOp.Model.create(args);}
        if (SubscriptionOp.View.create)       {SubscriptionOp.View.create(args);}
    }
    ,onInitialized: function() {
        if (SubscriptionOp.Controller.onInitialized) {SubscriptionOp.Controller.onInitialized();}
        if (SubscriptionOp.Model.onInitialized)      {SubscriptionOp.Model.onInitialized();}
        if (SubscriptionOp.View.onInitialized)       {SubscriptionOp.View.onInitialized();}
    }

    ,Controller: {
        create : function(args) {}
        ,onInitialized: function() {}

        ,MODEL_CHECKED_SUBSCRIPTION             : "subop-model-checked-subscription"          //param: userId, objectType, objectId, subscribed
        ,MODEL_CHECKED_SUBSCRIPTION_ERROR       : "subop-model-checked-subscription-error"    //param: userId, objectType, objectId, errorMsg
        ,MODEL_SUBSCRIBED_OBJECT                : "subop-model-subscribed-object"             //param: userId, objectType, objectId
        ,MODEL_SUBSCRIBED_OBJECT_ERROR          : "subop-model-subscribed-object-error"       //param: userId, objectType, objectId, errorMsg
        ,MODEL_UNSUBSCRIBED_OBJECT              : "subop-model-unsubscribed-object"           //param: userId, objectType, objectId
        ,MODEL_UNSUBSCRIBED_OBJECT_ERROR        : "subop-model-unsubscribed-object-error"     //param: userId, objectType, objectId, errorMsg

        ,VIEW_SUBSCRIBED_OBJECT                 : "subop-view-subscribed-object"              //param: userId, objectType, objectId
        ,VIEW_UNSUBSCRIBED_OBJECT               : "subop-view-unsubscribed-object"            //param: userId, objectType, objectId

        ,modelCheckedSubscription: function(userId, objectType, objectId, subsribed) {
            Acm.Dispatcher.fireEvent(this.MODEL_CHECKED_SUBSCRIPTION, userId, objectType, objectId, subsribed);
        }
        ,modelCheckedSubscriptionError: function(userId, objectType, objectId, errorMsg) {
            Acm.Dispatcher.fireEvent(this.MODEL_CHECKED_SUBSCRIPTION_ERROR, userId, objectType, objectId, errorMsg);
        }
        ,modelSubscribedObject: function(userId, objectType, objectId) {
            Acm.Dispatcher.fireEvent(this.MODEL_SUBSCRIBED_OBJECT, userId, objectType, objectId);
        }
        ,modelSubscribedObjectError: function(userId, objectType, objectId, errorMsg) {
            Acm.Dispatcher.fireEvent(this.MODEL_SUBSCRIBED_OBJECT_ERROR, userId, objectType, objectId, errorMsg);
        }
        ,modelUnsubscribedObject: function(userId, objectType, objectId) {
            Acm.Dispatcher.fireEvent(this.MODEL_UNSUBSCRIBED_OBJECT, userId, objectType, objectId);
        }
        ,modelUnsubscribedObjectError: function(userId, objectType, objectId, errorMsg) {
            Acm.Dispatcher.fireEvent(this.MODEL_UNSUBSCRIBED_OBJECT_ERROR, userId, objectType, objectId, errorMsg);
        }
        ,viewSubscribedObject: function(userId, objectType, objectId) {
            Acm.Dispatcher.fireEvent(this.VIEW_SUBSCRIBED_OBJECT, userId, objectType, objectId);
        }
        ,viewUnsubscribedObject: function(userId, objectType, objectId) {
            Acm.Dispatcher.fireEvent(this.VIEW_UNSUBSCRIBED_OBJECT, userId, objectType, objectId);
        }
    }

    ,Model: {
        create : function(args) {
            if (SubscriptionOp.Service.create) {SubscriptionOp.Service.create(args);}

            Acm.Dispatcher.addEventListener(SubscriptionOp.Controller.VIEW_SUBSCRIBED_OBJECT ,this.onViewSubscribedObject);
            Acm.Dispatcher.addEventListener(SubscriptionOp.Controller.VIEW_UNSUBSCRIBED_OBJECT ,this.onViewUnsubscribedObject);
        }
        ,onInitialized: function() {
            if (SubscriptionOp.Service.onInitialized) {SubscriptionOp.Service.onInitialized();}
        }

        ,onViewSubscribedObject: function(userId, objectType, objectId) {
            var isSubscribed = SubscriptionOp.Model.isSubscribed();
            if (Acm.isNotEmpty(isSubscribed)) {
                if (!isSubscribed) {
                    SubscriptionOp.Service.subscribe(userId, objectType, objectId);
                }
            }
        }
        ,onViewUnsubscribedObject: function(userId, objectType, objectId) {
            var isSubscribed = SubscriptionOp.Model.isSubscribed();
            if (Acm.isNotEmpty(isSubscribed)) {
                if (isSubscribed) {
                    SubscriptionOp.Service.unsubscribe(userId, objectType, objectId);
                }
            }
        }

        ,_subscribed: null
        ,isSubscribed: function() {
            return this._subscribed;
        }
        ,setSubscribed: function(subscribed) {
            this._subscribed = subscribed;
        }

        ,checkSubscription: function(userId, objectType, objectId) {
            SubscriptionOp.Service.checkSubscription(userId, objectType, objectId);
        }

        ,validateSubscriptions: function(data) {
            if (Acm.isNotArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateSubscription(data[i])) {
                    return false;
                }
            }

            return true;
        }
        ,validateSubscription: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.userId)) {
                return false;
            }
            if (Acm.isEmpty(data.subscriptionObjectType)) {
                return false;
            }
            if (Acm.isEmpty(data.objectId)) {
                return false;
            }
//            if (Acm.isEmpty(data.subscriptionId)) {
//                return false;
//            }
            return true;
        }
        ,validateUnsubscribe: function(data) {
            if (Acm.isEmpty(data)) {
                return false;
            }
            if (Acm.isEmpty(data.DeletedSubscriptionId)) {
                return false;
            }
            return true;
        }
        ,
    }

    ,Service: {
        create : function(args) {}
        ,onInitialized: function() {}

        ,API_CHECK_SUBSCRIPTION_ : "/api/v1/service/subscription/"
        ,API_SUBSCRIBE_          : "/api/v1/service/subscription/"
        ,API_UNSUBSCRIBE_        : "/api/v1/service/subscription/"

        ,checkSubscription: function(userId, objectType, objectId) {
            Acm.Service.asyncGet(
                function(response) {
                    if (response.hasError) {
                        SubscriptionOp.Controller.modelCheckedSubscriptionError(userId, objectType, objectId, response.errorMsg);

                    } else if (!SubscriptionOp.Model.validateSubscriptions(response)) {
                        SubscriptionOp.Controller.modelCheckedSubscriptionError(userId, objectType, objectId, "Invalidate subscription check data");

                    } else {
                        var subscribed = false;
                        for (var i = 0; i < response.length; i++) {
                            if (userId == response[i].userId
                                && objectType == response[i].subscriptionObjectType
                                && objectId == response[i].objectId) {
                                subscribed = true;
                                break;
                            }
                        }
                        SubscriptionOp.Model.setSubscribed(subscribed);
                        SubscriptionOp.Controller.modelCheckedSubscription(userId, objectType, objectId, subscribed);
                    }
                }
                ,App.getContextPath() + this.API_CHECK_SUBSCRIPTION_ + userId + "/" + objectType + "/" + objectId
            )
        }
        ,subscribe: function(userId, objectType, objectId) {
            Acm.Service.asyncPut(
                function(response) {
                    if (response.hasError) {
                        SubscriptionOp.Controller.modelSubscribedObjectError(userId, objectType, objectId, response.errorMsg);

                    } else if (!SubscriptionOp.Model.validateSubscription(response)) {
                        SubscriptionOp.Controller.modelSubscribedObjectError(userId, objectType, objectId, "Invalidate subscribe data");

                    } else if (userId     == response.userId
                            && objectType == response.subscriptionObjectType
                            && objectId   == response.objectId) {
                        SubscriptionOp.Model.setSubscribed(true);
                        SubscriptionOp.Controller.modelSubscribedObject(userId, objectType, objectId);
                    }
                }
                ,App.getContextPath() + this.API_SUBSCRIBE_ + userId + "/" + objectType + "/" + objectId
            )
        }
        ,unsubscribe: function(userId, objectType, objectId) {
            Acm.Service.asyncDelete(
                function(response) {
                    if (response.hasError) {
                        SubscriptionOp.Controller.modelUnsubscribedObjectError(userId, objectType, objectId, response.errorMsg);

                    } else if (!SubscriptionOp.Model.validateUnsubscribe(response)) {
                        SubscriptionOp.Controller.modelUnsubscribedObjectError(userId, objectType, objectId, "Invalidate unsubscribe data");

                    } else {
                        SubscriptionOp.Model.setSubscribed(false);
                        SubscriptionOp.Controller.modelUnsubscribedObject(userId, objectType, objectId);
                    }
                }
                ,App.getContextPath() + this.API_UNSUBSCRIBE_ + userId + "/" + objectType + "/" + objectId
            )

        }

    }

    ,View: {
        create : function(args) {
            if (SubscriptionOp.View.MicroData.create) {SubscriptionOp.View.MicroData.create();}

            this.getSubscriptionInfo = args.getSubscriptionInfo;
            this.$btnSubscribe = (args.$btnSubscribe)? args.$btnSubscribe : $("#btnSubscribe");
            this.$btnSubscribe.on("click", function(e) {SubscriptionOp.View.onClickBtnSubscribe(e, this);});

            Acm.Dispatcher.addEventListener(SubscriptionOp.Controller.MODEL_CHECKED_SUBSCRIPTION       ,this.onModelCheckedSubscription);
            Acm.Dispatcher.addEventListener(SubscriptionOp.Controller.MODEL_CHECKED_SUBSCRIPTION_ERROR ,this.onModelCheckedSubscriptionError);
            Acm.Dispatcher.addEventListener(SubscriptionOp.Controller.MODEL_SUBSCRIBED_OBJECT          ,this.onModelSubscribedObject);
            Acm.Dispatcher.addEventListener(SubscriptionOp.Controller.MODEL_SUBSCRIBED_OBJECT_ERROR    ,this.onModelSubscribedObjectError);
            Acm.Dispatcher.addEventListener(SubscriptionOp.Controller.MODEL_UNSUBSCRIBED_OBJECT        ,this.onModelUnsubscribedObject);
            Acm.Dispatcher.addEventListener(SubscriptionOp.Controller.MODEL_UNSUBSCRIBED_OBJECT_ERROR  ,this.onModelUnsubscribedObjectError);
        }
        ,onInitialized: function() {
            if (SubscriptionOp.View.MicroData.onInitialized) {SubscriptionOp.View.MicroData.onInitialized();}
        }

        ,MicroData: {
            create : function() {
                this.msgBtnSubscribeText = "Subscribe";
                this.msgBtnUnsubscribeText = "Unsubscribe";
                this.msgBtnErrorSubscribeText = "N/A";

                this.msgSubscribeSuccess = "Object subscribed";
                this.msgUnsubscribeSuccess = "Object unsubscribed";
            }
            ,onInitialized : function() {}
        }

        ,onModelCheckedSubscription: function(userId, objectType, objectId, subscribed) {
            var btnText = (subscribed)? SubscriptionOp.View.MicroData.msgBtnUnsubscribeText : SubscriptionOp.View.MicroData.msgBtnSubscribeText;
            SubscriptionOp.View.setTextBtnSubscribe(btnText)
            SubscriptionOp.View.setEnableBtnSubscribe(true);
        }
        ,onModelCheckedSubscriptionError: function(userId, objectType, objectId, errorMsg) {
            SubscriptionOp.View.setTextBtnSubscribe(SubscriptionOp.View.MicroData.msgBtnErrorSubscribeText);
            SubscriptionOp.View.setEnableBtnSubscribe(false);
            Acm.Dialog.error(errorMsg);
        }
        ,onModelSubscribedObject: function(userId, objectType, objectId) {
            SubscriptionOp.View.setTextBtnSubscribe(SubscriptionOp.View.MicroData.msgBtnUnsubscribeText);
            SubscriptionOp.View.setEnableBtnSubscribe(true);
            Topbar.Model.Flash.add(SubscriptionOp.View.MicroData.msgSubscribeSuccess);
        }
        ,onModelSubscribedObjectError: function(userId, objectType, objectId, errorMsg) {
            SubscriptionOp.View.setTextBtnSubscribe(SubscriptionOp.View.MicroData.msgBtnErrorSubscribeText);
            SubscriptionOp.View.setEnableBtnSubscribe(false);
            Acm.Dialog.error(errorMsg);
        }
        ,onModelUnsubscribedObject: function(userId, objectType, objectId) {
            SubscriptionOp.View.setTextBtnSubscribe(SubscriptionOp.View.MicroData.msgBtnSubscribeText);
            SubscriptionOp.View.setEnableBtnSubscribe(true);
            Topbar.Model.Flash.add(SubscriptionOp.View.MicroData.msgUnsubscribeSuccess);
        }
        ,onModelUnsubscribedObjectError: function(userId, objectType, objectId, errorMsg) {
            SubscriptionOp.View.setTextBtnSubscribe(SubscriptionOp.View.MicroData.msgBtnErrorSubscribeText);
            SubscriptionOp.View.setEnableBtnSubscribe(false);
            Acm.Dialog.error(errorMsg);
        }

        ,onClickBtnSubscribe: function(event, ctrl) {
            SubscriptionOp.View.setEnableBtnSubscribe(false);

            var info = SubscriptionOp.View.getSubscriptionInfo();
            var isSubscribed = SubscriptionOp.Model.isSubscribed();
            if (Acm.isNotEmpty(isSubscribed)) {
                if (isSubscribed) {
                    SubscriptionOp.Controller.viewUnsubscribedObject(info.userId, info.objectType, info.objectId);
                } else {
                    SubscriptionOp.Controller.viewSubscribedObject(info.userId, info.objectType, info.objectId);
                }
            }
        }

        ,setEnableBtnSubscribe: function(enable) {
            Acm.Object.setEnable(this.$btnSubscribe, enable);
        }
        ,setTextBtnSubscribe: function(text) {
            Acm.Object.setTextNodeText(this.$btnSubscribe, " " + text, -1);
        }
    }
};
