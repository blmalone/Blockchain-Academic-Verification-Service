Template.addContractAddressModal.helpers({
    account: function() {
        var accountAddress = Session.get('currentAccountAddress');
        console.log('Modal opened for: ' + accountAddress);
        if (typeof accountAddress !== "undefined") {
            var account = EthAccounts.findOne({address: accountAddress});
            return account;
        } else {
            return "";
        }
    }
});
Template.addContractAddressModal.events({
    'click #save': function (e) {
        e.preventDefault();
        var currentAccountAddress = Session.get('currentAccountAddress');
        var newSmartContract = {
            address: $('#newSmartContractAddress').val()
        };
        if (currentAccountAddress) {
            EthAccounts.update({address: currentAccountAddress}, {$set: {
                'contractAddress': newSmartContract.address
            }});
        }
        Modal.hide('addContractAddressModal');
    }
});