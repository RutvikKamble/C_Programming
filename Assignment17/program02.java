// WAP to check whether a number is a palindrome or not

class logic
{
    void CheckPalindrome(int iNo)
    {
        int iDigit = 0, iCnt = 0, iCheck = 0;

        iNo = iCheck;

        while (iNo > 0)
        {
            iCnt = iNo % 10;
            iDigit = iDigit * 10;
            iDigit = iDigit + iCnt;
            iNo = iNo % 10;
        }

        if(iDigit == iCheck)
        {
            System.out.println("Yes, the number is palindrom ");
        }
        else
        {
            System.out.println("No, the number is not palindrom ");
        }
    }    
}

class program02
{
    public static void main(String[] args)
    {
        
        logic obj = new logic();
        obj.CheckPalindrome(12345);
    
    }
}