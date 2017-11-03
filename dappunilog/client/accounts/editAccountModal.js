Template.editAccountModal.helpers({
    account: function() {
        var accountAddress = Session.get('currentAccountAddress');
        if (typeof accountAddress !== "undefined") {
            var account = EthAccounts.findOne({address: accountAddress});
            return account;
        } else {
            return "";
        }
    }
});

Template.editAccountModal.events({
    'click #save': function (e) {
        e.preventDefault();
        var currentAccountAddress = Session.get('currentAccountAddress');
        var newAccountNickName = {
            name: $('#newAccountNickName').val()
        };
        if (currentAccountAddress) {
            EthAccounts.update({address: currentAccountAddress}, {$set: {
                name: newAccountNickName.name
            }});
        }
        Modal.hide('editAccountModal');
    }
});