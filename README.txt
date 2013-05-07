Getting Started
======================================================

To use the API, you'll need API credentials.

curl https://keychain.oneid.com/register

To install, we require Maven (http://maven.apache.org/):

git clone git@github.com:OneID/oneid-java-sdk.git

cd oneid-java-sdk

Installation
======================================================

First, if your project uses Maven, I recommend running: mvn clean install

That will install our package into your .m2 directory.

Then you can just include us as a dependency in your project:

  <dependencies>
    <dependency>
      <groupId>com.oneid.rp</groupId>
      <artifactId>oneid-sdk</artifactId>
      <version>1.0-SNAPSHOT</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

If you don't use Maven, you can just run: mvn clean package, and then reference the jar in your project: oneid-sdk-1.0-SNAPSHOT.jar in target/.

Configuring
======================================================

After calling OneID.login() in Javascript, you should receive a payload similar to below:

{
    "nonces": {
        "repo": {
            "nonce": "eyJhbGciOiJFUzI1NiJ9.eyJub25jZSI6ICIwMDEyMDEzLTA1LTA3VDE5OjIyOjI2WjNvM2hLRSJ9.bpzYT6d-3tTI19vBxXXbpvehH6EeMJQ6-x6xDw97nORUlYxjh83x_HG5i0cw0MAyCOBfWzrvPBDzGc6cbAKbwA",
            "key": "y9Z-4zkgSS5sBIwMmb4oMgeSioGihqETb3FjPXJ1ppMw9dKA99VypD85ASyw51x52QCzi56xUDWMRh-uEW8mCg"
        },
        "ad": {
            "nonce": "eyJhbGciOiJFUzI1NiJ9.eyJub25jZSI6IjAwMTIwMTMtMDUtMDdUMTk6MjI6MjZaM28zaEtFIn0.5guCFvAdhZcMFFLg3ESyKPRnB3xiSo2ZpD5RuEAao31wRqs5uRQzxF7qTrQ4KdDtqgrSF85FI1__AxiYBcZSzQ",
            "key": "jgiY/fjelJEjdVXlKs9OCJN0OkjnQaeDoU2qG2Ng4pPDPIO/mBzn96aPvma02Xs8qU30sI/nxAXNp4ozPvfjJg"
        }
    },
...
    "uid": "KaTUyUUWoAn7ajRmWKjIFw==",
    "two_factor_token": "5anR6Zila+GWWYikXF4Uhw=="
}

There are two values you have to retain in order to trigger a second-factor authentication: "uid" and "two_factor_token".

"uid" is how you identify the user and link your internal account with OneID.
"two_factor_token" is a special token which, for security purposes, authorizes you to trigger two-factor requests on behalf of a user.

In Java, to trigger a server-side two-factor request, use the below snippet:

// We first instantiate the two-factor client with the API Key + API ID (which you can provision using: curl -d '{}' https://keychain.oneid.com/register)
// Ex:
// OneID2FClient client = new OneID2FClient([api key], [api ID]);
OneID2FClient client = new OneID2FClient("S7nB18z6DrM6RypXFsTxrg==", "052b0422-f23e-4e64-9e72-d6d3a45635b5");

// Then just trigger a two-factor request
// Ex:
// client.send([two factor token], [uid], [title of the two-factor request], [message for the two-factor request]);
OneIDResponse r = client.send("2zz5lQ1Zm9pjaqNm3Vs44w==", "XzwGbvDC1rCPe0sEQCiI0Q==", "Test Tile", "Test Message");

To know if the user pressed Accept or Deny, you can check r.isValid().  It returns a boolean.
If you want to know why r.isValid() came back False, you can check r.getResponse().
