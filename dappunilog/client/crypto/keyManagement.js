var ethSigUtil = require('eth-sig-util');

/**
 * This function is tasked with generating the public key of a user without directly
 * exposing their secret/private key in the process. A message is signed by users
 * private key. This is handled by a browser extension which manages key pairs securely.
 * The message is only signed with the confirmation of the user.
 * @param identity - current active account performing the message signing.
 */
getPublicKeyOfUser = function () {
    var msg = '0x879a053d4800c6354e76c7985a865d2922c82fb5b3f4577b2fe08b998954f2e01';
    var from = web3.eth.accounts[0];

    console.log('CLICKED, SENDING PERSONAL SIGN REQ');
    var params = [from, msg];
    var method = 'personal_sign';

    web3.currentProvider.sendAsync({
        method,
        params,
        from,
    }, function (err, result) {
        if (err) return console.error(err);
        if (result.error) return console.error(result.error);

        ModalHelper.pubKeyModal('generateKeyModal', result.result);
        alert('PERSONAL SIGNED:' + JSON.stringify(result.result));

        console.log('recovering...');
        const msgParams = {data: msg};
        msgParams.sig = result.result;
        console.dir({msgParams});

        var actualPubKey = '0xaa5b55ed4fa285bbd704031391b76cc57c13626e42288dbb902' +
            'c7cc5c456522ec0eb1724b3c56a076ca7836300f23c8e6f791ca4a4dcdf5743cb53c5e979af80';
        const publicKey = ethSigUtil.extractPublicKey(msgParams);
        if (publicKey === actualPubKey) {
            console.log('Success it works Blaine!!');

        } else {
            console.log('the pub keys did not match!');
        }
        const recovered = ethSigUtil.recoverPersonalSignature(msgParams);
        console.dir({recovered})

        if (recovered === from) {
            alert('SigUtil Successfully verified signer as ' + from);
        } else {
            alert('SigUtil Failed to verify signer when comparing ' + recovered.result + ' to ' + from);
            console.log('Failed, comparing %s to %s', recovered, from);
        }
    });
};
