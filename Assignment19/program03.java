// WAP to check whether a number is divisible by 5 and 11 or not.

class logic
{
    void CheckDivisible(int iNo)
    {
        if(iNo % 5 == 0 && iNo % 11 == 0)
        {
            System.out.println("Yes, The number is divisible by 5 and 11");
        }
        else
        {
            System.out.println("No, The number is not divisible by 5 and 11");
        }
    }
}

class program03
{
    public static void main(String[] args)
    {
        logic obj = new logic();
        obj.CheckDivisible(165);
        
    }
}