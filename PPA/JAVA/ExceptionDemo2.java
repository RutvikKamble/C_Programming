import java.util.*;

class ExceptionDemo2
{
    public static void main(String A[]) 
    {
        Scanner sobj = new Scanner(System.in);

        int iNo1 = 0, iNo2 = 0;
        float fAns = 0;
        
        System.out.println("Enter first number");
        iNo1 = sobj.nextInt();
        
        System.out.println("Enter second number");
        iNo2 = sobj.nextInt();
        
        fAns = (float)iNo1 / (float)iNo2;       // Type casting  
        System.out.println("Division :"+fAns);

    }
}
