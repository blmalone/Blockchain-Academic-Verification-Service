var ipfsAPI = require('ipfs-api');

var ipfs = ipfsAPI();

Template.Records.onCreated(function () {
    this.showNoRecordsContent = new ReactiveVar(false);
});

Template.Records.events({
    'click .getRecordsOnContract': function (e, template) {
        var accountId = FlowRouter.getParam('id');
        var account = EthAccounts.findOne({_id: accountId});
        var Unilog = web3.eth.contract(unilogABI);
        var contractInstance = Unilog.at('0x' + account.contractAddress);

        if(!contractInstance) {
            //if we do not have any records or no contract address then set the hidden error message to visible.
            template.showNoRecordsContent.set(true);
            console.log('No contract currently deployed.');
            return false;
        }
        contractInstance.verifyArtifact.call(0, function(err, result) {
            if(err) {
                console.log('error : ' + err);
                return err;
            }
            if(result) {
                console.log('Successfully called contract function.');
                console.log('Result: ' + result);
                console.log("Content Address: " + result);
                getIPFSData(result, accountId);
            }
        });
        template.showNoRecordsContent.set(false);
    }
});

Template.Records.helpers({
    account: () => {
        var accountId = FlowRouter.getParam('id');
        return EthAccounts.findOne({_id: accountId});
    },
    'contractNotSet': function () {
        var accountId = FlowRouter.getParam('id');
        var account = EthAccounts.findOne({_id: accountId});
        if (account.contractAddress === undefined) {
            console.log('contractAddress is undefined: ' + account.contractAddress);
            return true;
        } else {
            console.log('contractAddress is : ' + account.contractAddress);
            return false;
        }
    },
    'showNoRecordsContent': function () {
        return Template.instance().showNoRecordsContent.get();
    }
});

var getIPFSData = function (hash, accountId) {
    ipfs.cat(hash, {buffer: true}, function (err, res) {
        if (err || !res) {
            return console.error('ipfs cat error', err, res)
        }
        result = JSON.parse(res);
        EthAccounts.update({_id:accountId}, {$set: {
            currentContent: result
        }});
    });
};