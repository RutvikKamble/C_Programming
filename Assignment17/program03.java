// WAP to find the maximum of two numbers

class logic
{
    void CheckMax(int iNo1, int iNo2)
    {        

        if(iNo1 > iNo2)
        {
            System.out.println("First number is max");   
        }
        else    
        {
            System.out.println("Second number is max");  
        }
    }    
}

class program03
{
    public static void main(String[] args)
    {
        logic obj = new logic();
        obj.CheckMax(12,14);
    
    }
}