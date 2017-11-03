unilogABI = [{"constant":false,"inputs":[{"name":"artifactAddress","type":"string"},{"name":"validFor","type":"uint256"}],"name":"addArtifact","outputs":[{"name":"","type":"uint256"}],"payable":false,"type":"function"},{"constant":true,"inputs":[],"name":"version","outputs":[{"name":"","type":"uint256"}],"payable":false,"type":"function"},{"constant":false,"inputs":[{"name":"artifactId","type":"uint256"}],"name":"deleteArtifact","outputs":[{"name":"","type":"bool"}],"payable":false,"type":"function"},{"constant":false,"inputs":[{"name":"artifactOwnerAddress","type":"address"}],"name":"assignArtifactOwner","outputs":[{"name":"","type":"bool"}],"payable":false,"type":"function"},{"constant":true,"inputs":[{"name":"artifactId","type":"uint256"}],"name":"verifyArtifact","outputs":[{"name":"","type":"string"}],"payable":false,"type":"function"}];

unilogRegistryABI = [{"constant":true,"inputs":[],"name":"version","outputs":[{"name":"","type":"uint256"}],"payable":false,"type":"function"},{"constant":false,"inputs":[{"name":"contractAddress","type":"address"}],"name":"contains","outputs":[{"name":"","type":"bool"}],"payable":false,"type":"function"},{"constant":false,"inputs":[{"name":"contractAddress","type":"address"}],"name":"addContract","outputs":[],"payable":false,"type":"function"},{"constant":false,"inputs":[{"name":"contractAddress","type":"address"}],"name":"disableContract","outputs":[],"payable":false,"type":"function"}];

source = "pragma solidity ^0.4.0;\n\n" +
    "contract Unilog {\n" +
    "   address private unilog = msg.sender;\n" +
    "   address private artifactOwner;\n" +
    "   struct Artifact {\n" +
    "       string artifactAddress;\n" +
    "       bool active;\n" +
    "       uint expirationDate;\n" +
    "   }\n" +
    "   Artifact[] private artifacts;\n\n" +
    "   function addArtifact(string artifactAddress, uint validFor) onlyBy(unilog) returns(uint) {\n" +
    "       uint expirationDate;\n" +
    "       if(validFor == 0) {\n" +
    "           expirationDate = validFor;\n" +
    "       } else {\n" +
    "           expirationDate = now + (validFor * 1 weeks);\n" +
    "       }\n" +
    "       return artifacts.push(Artifact(artifactAddress, true, expirationDate));\n" +
    "   }\n\n" +
    "   function verifyArtifact(uint artifactId) constant returns (string) {\n" +
    "       if(artifactId < artifacts.length) {\n" +
    "           Artifact current = artifacts[artifactId];\n" +
    "           if(current.expirationDate == 0 || now < current.expirationDate) {\n" +
    "               return current.artifactAddress;\n" +
    "           } else {\n" +
    "               return \"Expired!\";" +
    "           }\n" +
    "       } else {\n" +
    "           return \"Fictional Artifact!\";\n" +
    "       }\n" +
    "   }\n\n" +
    "   function deleteArtifact(uint artifactId) onlyBy(artifactOwner)returns(bool){\n" +
    "       if(artifactId < artifacts.length) {\n" +
    "           delete artifacts[artifactId];return true;\n" +
    "       } else {\n" +
    "           return false;\n" +
    "       }\n" +
    "   }\n\n" +
    "   function assignArtifactOwner(address artifactOwnerAddress) onlyBy(unilog) returns(bool) {\n" +
    "       if (artifactOwnerAddress != unilog) {\n" +
    "           artifactOwner = artifactOwnerAddress;\n" +
    "           return true;\n" +
    "       }\n" +
    "       return false;\n" +
    "   }\n\n" +
    "   modifier onlyBy(address _account) {\n" +
    "       if (msg.sender != _account)\n" +
    "           throw;\n" +
    "       _;\n" +
    "   }\n\n" +
    "   function version() constant returns (uint) { return 1; }\n" +
    "}";