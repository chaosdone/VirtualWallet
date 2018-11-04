# VirtualWallet

Author: Claudia Zhou

Technology stack: 
	Java, Spring, Hibernate, Thymeleaf

Assumption: 
	Since one user only has one wallet, for the sake of this tech challenge, User modal is directly linked with transaction account, thus not providing endpoint to create a new wallet for user. Endpoint is provided for create a new user.
	Since Hibernate(in-store memory store) doesn't support dynamically generate table feature, this demo is only demonstrating two transaction accounts(primary and savings). Multiple transaction account feature (including create new trasanction account) should be able to be extended in the future.
	Testing for concurrent is not implemented as testing with multithread(e.g.testng) doesn't seem like supported by Spring.

Instruction on running:
	1.set up local server on portal 3306
	2.go to application.properties and change username & password to your local server credentials
	3.create schema manually
		CREATE SCHEMA `virtualwallet` ;

	4.mvn spring-boot:run
	5.manually add in user role
		INSERT INTO `virtualwallet`.`role` (`role_id`, `name`) VALUES ('0', 'ROLE_USER');
	6.open browser and go to localhost:8080
	7.you are good to go!