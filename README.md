# Blockchain Academic Verification Service
Code not yet public, please send me an email at bmalone05@qub.ac.uk to request.

The corresponding code for the research paper I wrote early 2017. You can find that [here](https://github.com/blmalone/Papers-and-Resume/blob/master/Dissertation_Blaine%20Malone.pdf).
Main features of the application can be found in the paper.

Currently lacking in **integration/unit tests** for various components of the system. For the purposes of the research paper all the **smart contract code is extensively tested**. If application ever enters a production environment then test coverage will be dramatically increased before.

For a system demo, refer to this youtube video: [System Demo](https://www.youtube.com/watch?v=3-YWdP2X6pw)

---

The system demo referenced in the above link is from **v1.0** of the application. Since then, the application has more robust security:

**Key Features - v1.1**
 
- Adoption of HTTPS : `keytool -genkey -alias tomcat -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore keystore.p12 -validity 3650`
- Passwords no longer stored in plain text. Now using `BcryptPasswordEncoder` to hash the passwords with a randomly generated salt.
- Users now have the option of creating their own password. Pre-generated password no longer sent to their email address. 
- Passwords Policy implemented: must contain at least 1 uppercase letter, 1 lowercase letter, 1 number, 1 special character. It also needs to have a minimum length of 8 characters.
