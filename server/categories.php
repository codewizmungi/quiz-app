<?php 

    //GET All Categories API
    
    include 'includes/database.php';
    
    $sql = '';
        
    $json = array();
        
        $sql = "SELECT quiz_questions.category, COUNT(*) AS category_total_questions FROM quiz_questions GROUP BY quiz_questions.category ORDER BY RAND()";
            
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
