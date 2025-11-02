import java.util.*;

class Demo
{
    float AdditionTwoNumbers(float fNo1, float fNo2) 
    {
        float fSum = 0;
        fSum = fNo1 + fNo2;
        return fSum;
    }
}
class lbadditon
{
    public static void main(String A[])
    {
        Demo obj=new Demo();
        Scanner sobj = new Scanner(System.in);

        float fValue1 = 0, fValue2 = 0; 
        float fRat = 0;

        System.out.println("Enter first Number");
        fValue1 = sobj.nextFloat();

        System.out.println("Enter second Number");
        fValue2 = sobj.nextFloat();

        fRat = obj.AdditionTwoNumbers(fValue1, fValue2);

        System.out.println("Additoin is : "+fRat);
        //sobj.close();
    }
}