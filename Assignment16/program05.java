// WAP to count the number of digits in a given number

class Logic
{
    int iCnt = 0, iDigit = 0;

    void reverseNumber(int iNo)
    {
        while (iNo > 0)
        {
            iDigit = iNo % 10;
            iCnt = ++iCnt;
            iNo = iNo / 10;
        }
        System.out.println(iCnt);
    }
}

class program05
{
    public static void main(String args[])
    {
        Logic obj = new Logic();
        obj.reverseNumber(1234567);
    }
}