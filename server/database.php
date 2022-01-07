<?php

    $DB_HOST = 'localhost';
    $DB_NAME = 'DATABASE NAME';
    $DB_USERNAME = 'DATABASE USERNAME';
    $DB_USER_PASSWORD = 'DATABASE USER PASSWORD';

    try {
        
    	$conn = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_user_pass);
    	$conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    	
    } catch (PDOException $e) {
    
        echo 'Connection Error: '. $e->getMessage();
        
    }

?>
