// WAP to print the multiplication table of number

class logic
{
    void CheckSmallNo(int iNo)
    {     
        int iCnt = 0, iDigit = 0;

        iCnt = 1;
        iDigit = 1;

        while(iCnt <= 10)
        {
            iDigit = iNo * iCnt;
            System.out.println(iDigit);
            iCnt = ++iCnt;
        }
    } 
}

class program05
{
    public static void main(String[] args)
    {
        logic obj = new logic();
        obj.CheckSmallNo(8);
    
    }
}