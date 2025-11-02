// WAP to find the maximum of two numbers

class logic
{
    void CheckSmallNo(int iNo1, int iNo2, int iNo3)
    {        

        if(iNo1 < iNo2 && iNo1 < iNo3)
        {
            System.out.println("First number is small");   
        }
        else
        {
            if(iNo2 < iNo3)
            {
                System.out.println("Second number is small");   
            } 
            else
            {
                System.out.println("Third number is small");   
            } 
        }
    }    
}

class program04
{
    public static void main(String[] args)
    {
        logic obj = new logic();
        obj.CheckSmallNo(10,11,15);
    
    }
}