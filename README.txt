To use the API, you'll need API credentials.

curl https://keychain.oneid.com/register

To install, we require Maven (http://maven.apache.org/):

git clone git@github.com:OneID/oneid-java-sdk.git

cd oneid-java-sdk

mvn clean install

You can add as a dependency by adding the following to your project's pom.xml:

  <dependencies>
    <dependency>
      <groupId>com.oneid.rp</groupId>
      <artifactId>oneid-sdk</artifactId>
      <version>1.0-SNAPSHOT</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
