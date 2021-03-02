<?php
// Following solution class contains, function doesCircleExist to check if circle exist ?
//We can add additional validation to check if command input is valid
// To increase readbility added constants and multiple if else condition 



class Solution {

    CONST NORTH ='N';
    CONST SOUTH = 'S';
    CONST EAST = 'E';
    CONST WEST = 'W';
    CONST CONSOLE= 'php://output';
    /**
     * @param String $commands
     * @return String
     */
    function doesCircleExist($commands) {
        $direction = 'N';
        $x_cord = 0;
        $y_cord = 0;
        $cmd_len= strlen($commands);
        
        for ($i = 0; $i < $cmd_len; $i++) {
            switch ($commands[$i]) {
                case 'G':// Go Ahead
                    if ($direction == self::NORTH) { $x_cord++; }
                    if ($direction == self::SOUTH) { $x_cord--; }
                    if ($direction == self::EAST) { $x_cord++; }
                    if ($direction == self::WEST) { $x_cord--; }
                    break;
                case 'L':// Turn Left
                    if ($direction == self::NORTH) { $direction = 'W'; break; }
                    if ($direction == self::SOUTH) { $direction = 'E'; break; }
                    if ($direction == self::EAST) { $direction = 'N'; break; }
                    if ($direction == self::WEST) { $direction = 'S'; break; }
                    break;
                case 'R':// Turn Right
                    if ($direction == self::NORTH) { $direction = 'E'; break; }
                    if ($direction == self::SOUTH) { $direction = 'W'; break; }
                    if ($direction == self::EAST) { $direction = 'S'; break; }
                    if ($direction == self::WEST) { $direction = 'N'; break; }
                    break;
            }
        }
        
        if ($direction != self::NORTH || ($x_cord == 0 && $y_cord == 0)) {
            return 'YES';
        } else {
            return 'NO';
        }
        
    }
    
    /**
     * Function to validate if Proper input is provided
     */
    public function validateInputOne($n)
    {
        $n = trim($n);
        if( (filter_var($n, FILTER_VALIDATE_INT) === false ) && $n == 0 && $n > 10)
        {
                return false;        
        }
        return true;
    }
    
    /**
    */
    public function writeToConsole($res)
    {
        $out = fopen(SELF::CONSOLE, 'a+'); //output handler
        fputs($out, $res); //writing output operation
        fclose($out); //closing handler

    }

}


function runRobot()
{
    $command_n = null;
    $command_n = readline("Please enter how many commands for Robot ?\n");
    $robotCicleCheck = new Solution();
    if($robotCicleCheck->validateInputOne($command_n))
    {
        $commands_input= array();
        for($k=0;$k<$command_n;$k++)
        {
            $commands_input[$k] = readline(PHP_EOL);
        }
                
        //Find out if Circle exist or not
        
        if(isset($commands_input))
        {
           $res = null;
           foreach ($commands_input as $key=>$cmd) {
               //Call Cirlcle exist function
               $res = $res.$robotCicleCheck->doesCircleExist($cmd);
               $res = $res.PHP_EOL;
               
           }
           
           $robotCicleCheck->writeToConsole($res);
        }
        unset($res);
        unset($robotCicleCheck);
        unset($n);
    }
    else
    {
        echo 'INVALID_INPUT_NUMBER';
    }
}
runRobot();// Main Function