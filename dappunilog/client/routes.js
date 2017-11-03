Accounts.onLogin(function () {
    FlowRouter.go('overview');
});

Accounts.onLogout(function () {
    FlowRouter.go('home');
});

FlowRouter.triggers.enter([function(context, redirect) {
    if(!Meteor.userId()){
        if(context.path == '/FAQ') {
            FlowRouter.go('FAQ');
        } else {
            FlowRouter.go('home');
        }
    }
}]);

FlowRouter.route('/', {
    name: 'home',
    action() {
        if(Meteor.userId()) {
            FlowRouter.go('overview')
        }
        BlazeLayout.render('HomeLayout');
    }
});

FlowRouter.route('/FAQ', {
    name: 'FAQ',
    action() {
        BlazeLayout.render('FAQ');
    }
});

FlowRouter.route('/overview', {
    name: 'overview',
    action() {
        BlazeLayout.render('MainLayout', {main: 'Accounts'});
    }
});

FlowRouter.route('/notifications', {
    name: 'notifications',
    action() {
        BlazeLayout.render('MainLayout', {main: 'Notifications'});
    }
});


FlowRouter.route('/verifier/:id', {
    name: 'verifier',
    action() {
        BlazeLayout.render('MainLayout', {main: 'VerifierPortal'});
    }
});

FlowRouter.route('/account/:id', {
    name: 'account',
    action() {
        BlazeLayout.render('MainLayout', {main: 'AccountSingle'});
    }
});

FlowRouter.route('/records/:id', {
    name: 'records',
    action() {
        BlazeLayout.render('MainLayout', {main: 'Records'});
    }
});