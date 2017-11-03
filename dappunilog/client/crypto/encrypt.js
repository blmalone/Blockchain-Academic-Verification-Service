/**
 * Encrypt the plaintext with the publicKey of identity
 * @param  {{privateKey: ?string, publicKey: string}} identity
 * @param  {string} message
 * @return {string}
 */
encrypt = function (identity, message) {
    //sign a message using the metamask interface --> prompt user instead of manually exposing their privKey.
    var privateKeyFromMetaMask = 'e9813a2a01eca824e0081c56d54d314639fcc11d5294655b559fbe4bd69f3657';
    const privKeyHex = privateKeyFromMetaMask;
    const privKey = new Buffer(privKeyHex, 'hex')
    const msgParams = {data: message};
    const signed = ethSigUtil.personalSign(privKey, msgParams);

    //Extract publicKey from signed message.
    msgParams.sig = signed;
    const publicKey = ethSigUtil.extractPublicKey(msgParams);

    if (publicKey === '0x6a5819cba76cb7823d5d6d0b0f51c783dd468' +
        '09b499a68f7bbd1c4da7bb702be86719528738c9e780390ed1ec163' +
        '704f681c01b7014e9a0b7c457a55513613c7') {
        console.log('Success! It worked!... The library extracted the correct public key.');
        console.log('pubKey: ' + publicKey);
    } else {
        console.log('It failed, try again!');
        console.log('pubKey: ' + publicKey);
    }

    //sign message (foobar) with my ether wallet priv key
    // const privKeyHex = identity.privateKey;
    // const privKey = new Buffer(privKeyHex, 'hex')
    // const msgParams = { data: message };
    // const signed = ethSigUtil.personalSign(privKey, msgParams);
    // //
    // // //Extract publicKey from signed message.
    // msgParams.sig = signed;
    // const publicKey = ethSigUtil.extractPublicKey(msgParams);

    /*
     * this key is used as false sample, because bitcore would crash when alice has no privateKey
     */
    //var privKey = new bitcore.PrivateKey('642bf38c84e4997a9f8c2d46cdad47d8c6cbc0e55fb130182273980256a5206d');
    //console.log('privkey: ' + privKey);
    //var alice = ECIES().privateKey(privKey).publicKey(new bitcore.PublicKey(identity.publicKey));
    //var alice = new bitcore.PublicKey.fromPrivateKey(privKey);

    //console.log('pubkey: ' + alice);
    //var encrypted = alice.encrypt(message);

    //return encrypted.toString('hex');
    return 'done';
};