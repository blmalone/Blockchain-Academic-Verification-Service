Template.AccountSingle.onCreated(function (){
});
Template.AccountSingle.events({
    'click .open-edit-name-modal': function (e) {
        e.preventDefault();
        var account = $(e.target).closest('.account');
        var accountAddress = account.attr('data-id');
        ModalHelper.openModalFor(accountAddress, 'editAccountModal');
    },
    'click .getPubKey': function () {
        var message = "foobar";
        getPublicKeyOfUser(message);
    },
    'click .addContractAddress': function(e) {
        e.preventDefault();
        var account = $(e.target).closest('.account');
        var accountAddress = account.attr('data-id');
        console.log('Opening modal for ' + accountAddress + ' to add their contract address');
        ModalHelper.openModalFor(accountAddress, 'addContractAddressModal');
    }
});

Template.AccountSingle.helpers({
    account: ()=> {
        var accountId = FlowRouter.getParam('id');
        return EthAccounts.findOne({_id: accountId});
    },
    'contractNotSet': function () {
        var accountId = FlowRouter.getParam('id');
        var account = EthAccounts.findOne({_id: accountId});
        if (account.contractAddress === undefined) {
            return true;
        } else {
            return false;
        }
    }
});