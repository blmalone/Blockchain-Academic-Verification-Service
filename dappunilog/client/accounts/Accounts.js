//local collection
UserCollection = new Mongo.Collection('userCollection', {connection: null});
//local persistence collection
UserCollectionObserver = new PersistentMinimongo(UserCollection);

//Uncomment the line below to remove entries from the DB and restart. Ensure to add comment back.
 //UserCollection.remove({});

//this allows us to insert data from the client into out collection. Only is the user is signed in i.e. the have an id.
UserCollection.allow({
    insert: function (userId, doc) {
        return !!userId;
    },
    update: function(userId, doc) {
        return !!userId;
    },
    remove: function (userId, doc) {
        return !!userId;
    }
});

DependentsSchema = new SimpleSchema({
    name: {
        type: String
    },
    age: {
        type: String
    }
});

NewAccountSchema = new SimpleSchema({
    name: {
        type: String,
        label: "Name"
    },
    desc: {
        type: String,
        label: "Description"
    },
    dependents: {
        type: [DependentsSchema]
    },
    author: {
        type: String,
        label: "Author",
        autoValue: function(){
            return this.userId;
        },
        autoform: {
            afFieldInput: {
                type: "hidden"
            },
            afFormGroup: {
                label: false
            }
        }
    },
    createdAt: {
        type: Date,
        label: "CreatedAt",
        autoValue: function() {
            return new Date();
        },
        autoform: {
            afFieldInput: {
                type: "hidden"
            },
            afFormGroup: {
                label: false
            }
        }
    },
    isEdited: {
        type: Boolean,
        defaultValue: false,
        optional: true,
        autoform: {
            afFieldInput: {
                type: "hidden"
            },
            afFormGroup: {
                label: false
            }
        }
    }
});

UserCollection.attachSchema(NewAccountSchema);

Template.Accounts.onCreated(function (){
});

Template.Accounts.helpers({
    accounts: ()=> {
        return UserCollection.find({});
    },
    /**
     Convert Wei to Ether Values
     @method (fromWei)
     */
    'fromWei': function(weiValue, type){
        return web3.fromWei(weiValue, type).toString(10);
    },
    /**
     Returns boolean indicating if there are any active accounts
     @method (noActiveAccounts)
     */
    'noActiveAccounts': function(){
        return (web3.eth.accounts[0] === undefined);
    },
    /**
     Get Eth Accounts
     @method (ethAccounts)
     */
    'ethAccounts': function(){
        return EthAccounts.findAll({});
    },
    /**
     Get One Ethereum Account
     @method (getOneEthAccount)
     */
    'getOneEthAccount': function(){
        return EthAccounts.findOne({});
    },
    'displayAllAccounts': function () {
        return Session.get('showAllAccounts');
    }
});

Template.Accounts.onRendered(function(){
});

Template.Accounts.events({
});

