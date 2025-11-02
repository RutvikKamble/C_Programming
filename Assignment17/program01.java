// WAP to find the sum of digit of a number

class logic
{
    int iSum = 0, iDigit = 0;

    void sumDigit(int iNo)
    {
        while (iNo > 0)
        {
        iDigit = iNo % 10;
        iSum = iSum + iDigit;
        iNo = iNo / 10;
        }
        System.out.println(iSum);
    }
    
}

class program01
{
    public static void main(String[] args)
    {
        logic obj = new logic();
        obj.sumDigit(12345);
    }
}