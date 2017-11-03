var ipfsAPI = require('ipfs-api');

var ipfs = ipfsAPI();

Template.VerifierPortal.onCreated(function () {
    this.showNoResult = new ReactiveVar(true);
    Session.set('result', 'Please enter a valid token above to view the artifact.');
});

Template.VerifierPortal.events({
    'submit .form-verify': function (event, template) {
        event.preventDefault();
        var contractAddress = event.target.contractAddress.value;
        var artifactId = event.target.artifactId.value;
        var registryAddress = event.target.registryAddress.value;

        //True is all variables are set.
        if (contractAddress && artifactId && registryAddress) {
            var Unilog = web3.eth.contract(unilogABI);
            var contractInstance = Unilog.at('0x' + contractAddress);

            var UnilogRegistry = web3.eth.contract(unilogRegistryABI);
            var registryContractInstance = UnilogRegistry.at('0x' + registryAddress);

            if (!contractInstance || !registryContractInstance) {
                //if we do not have any records or no contract address then set the hidden error message to visible.
                console.log('No contract currently deployed.');
                return;
            }

            registryContractInstance.contains.call('0x' + contractAddress, function (err, result) {
                if (err) {
                    console.log('Error when querying Unilog Registry: ' + err);
                    return err;
                }
                if (result) {
                    getArtifactFromContract(contractInstance, artifactId, template);
                } else {
                    Session.set('result', 'The contract address is either not in the Unilog Registry or you have '
                        + 'entered the registry address incorrectly. Be careful, you must enter the correct Unilog '
                        + 'address so that you can trust the data you receive.');
                    template.showNoResult.set(true);
                    return;
                }
            });
        } else {
            console.log("Please enter all fields.");
        }
    }
});

Template.VerifierPortal.helpers({
    account: () => {
        var accountId = FlowRouter.getParam('id');
        return EthAccounts.findOne({_id: accountId});
    },
    source: function () {
        return source;
    },
    result: function () {
        return Session.get('result');
    },
    showNoResult: function () {
        return Template.instance().showNoResult.get();
    }
});

var getArtifactFromContract = function (contractInstance, artifactId, template) {
    contractInstance.verifyArtifact.call(artifactId, function (err, result) {
        if (err) {
            console.log('error : ' + err);
            return err;
        }
        if (result) {
            if (result != 'Fictional Artifact!'
                && result != 'Expired!') {
                getIPFSData(result);
                template.showNoResult.set(false);
            } else {
                Session.set('result', 'This token is not valid, message received: ' + result);
                template.showNoResult.set(true);
            }
        }
    });
};

var getIPFSData = function (hash) {
    ipfs.cat(hash, {buffer: true}, function (err, res) {
        if (err || !res) {
            return console.error('ipfs cat error', err, res)
        }
        var result = JSON.parse(res);
        if (result.pass == true) {
            result.pass = 'Passed';
        } else {
            result.pass = 'Failed';
        }
        Session.set('result', result);
    });
};
