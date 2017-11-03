Template.SideNav.onCreated(function (){
});

Template.SideNav.helpers({
    account: ()=> {
        return EthAccounts.findOne({});
    }
});