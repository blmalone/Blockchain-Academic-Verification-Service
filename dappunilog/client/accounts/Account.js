var bitcore = require('bitcore-lib');

Template.Account.onCreated(function(){
    this.accountAddress = new ReactiveVar(Session.get(this._id));
});

Template.Account.onRendered(function(e){
});

Template.Account.events({
    'click #add': function(e) {
    },
    'click .open-edit-name-modal': function (e) {
        e.preventDefault();
        var account = $(e.target).closest('.account');
        var accountAddress = account.attr('data-id');
        ModalHelper.openModalFor(accountAddress, 'editAccountModal');
    },
    'click .getPubKey': function () {
        var message = "foobar";
        getPublicKeyOfUser(message);
    }
});

Template.Account.helpers({
    /**
     Get the current account
     @method (account)
     */
    'isActive': function(currentAccount) {
        return (web3.eth.accounts[0] === currentAccount);
    },
});



