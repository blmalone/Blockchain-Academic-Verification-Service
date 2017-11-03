pragma solidity ^0.4.0;

contract Unilog {

    address private unilog = msg.sender;
    address private artifactOwner;

    struct Artifact {
        bytes artifactAddress;
        bool active;
        uint expirationDate;
    }

    Artifact[] private artifacts;

    function addArtifact(bytes artifactAddress, uint validFor)
                                                onlyBy(unilog) returns(uint) {
        uint expirationDate;
        if(validFor == 0) {
            expirationDate = validFor;
        } else {
            expirationDate = now + (validFor * 1 seconds);
        }

        return artifacts.push(Artifact(artifactAddress, true, expirationDate));
    }

    function verifyArtifact(uint artifactId) returns (bytes) {
        if(artifactId < artifacts.length) {
            Artifact current = artifacts[artifactId];
            if(current.active) {
                if(current.expirationDate == 0 || now < current.expirationDate) {
                    return current.artifactAddress;
                } else {
                    artifacts[artifactId].active = false;
                    return "Expired!";
                }
            } else {
                return "Expired!";
            }
        } else {
            return "Fictional Artifact!";
        }
    }

    function deleteArtifact(uint artifactId) onlyBy(artifactOwner) returns(bool) {
        if(artifactId < artifacts.length) {
            delete artifacts[artifactId];
            return true;
        } else {
            return false;
        }
    }

    function assignArtifactOwner(address artifactOwnerAddress) onlyBy(unilog) returns(bool) {
        if (artifactOwnerAddress != unilog) {
            artifactOwner = artifactOwnerAddress;
            return true;
        }
        return false;
    }

    modifier onlyBy(address _account) {
        if (msg.sender != _account)
            throw;
        _;
    }

    function version() constant returns (uint) { return 1; }
}