<?php 

    //GET All Questions by Category API
    
    include '../../includes/database.php';
    
    $category = $_POST['category'];
    
    $sql = '';
        
    $json = array();
        
        $sql = "SELECT * FROM quiz_questions WHERE category = '$category' ORDER BY RAND()";
            
        $statement = $conn->prepare($sql);
        $statement->execute();
        
        if($statement->rowCount()) {
            
            while($row = $statement->fetch(PDO::FETCH_ASSOC)){
                $json[] = $row;
            }
                     
        } elseif(!$statement->rowCount()) {
                
            $json['status'] = 0;
            $json['message'] = "No Data Found";
                
        }  
        
        header('Content-Type:Application/json');
        echo json_encode($json); 
        
    
    
?>
