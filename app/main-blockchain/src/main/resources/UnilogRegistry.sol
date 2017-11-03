pragma solidity ^0.4.0;

contract UnilogRegistry {

    address private owner = msg.sender;

    mapping(address => bool) repository;

    function addContract(address contractAddress) onlyBy(owner) {
        repository[contractAddress] = true;
    }

    function disableContract(address contractAddress) onlyBy(owner) {
        repository[contractAddress] = false;
    }

    function contains(address contractAddress) returns (bool) {
        return repository[contractAddress];
    }

    modifier onlyBy(address _account) {
        if (msg.sender != _account)
            throw;
        _;
    }

    function version() constant returns (uint) { return 1; }
}