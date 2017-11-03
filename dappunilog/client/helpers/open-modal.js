ModalHelper = {};

ModalHelper.openModalFor = function(currentAccountAddress, modalToOpen) {
    Session.set('currentAccountAddress', currentAccountAddress);
    Modal.show(modalToOpen);
};

ModalHelper.pubKeyModal = function(modalToOpen, context) {
    Modal.show(modalToOpen, context);
};