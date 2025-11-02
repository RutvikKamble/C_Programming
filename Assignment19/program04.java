// WAP to print each digit of number separately

class logic
{
    void PrintDigit(int iNo)
    {
        int iDigit = 0;

        while (iNo > 0)
        {
            iDigit = iNo % 10;
            System.out.println(iDigit);
            iNo = iNo / 10;
        }
    }
}


class program04
{
    public static void main(String[] args)
    {
        logic obj = new logic();
        obj.PrintDigit(1234); 

    }
}
