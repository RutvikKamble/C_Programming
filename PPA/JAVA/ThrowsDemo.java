import java.util.Scanner;

class Demo
{
    public int Division(int No1, int No2) throws ArithmeticException
    {
        int Ans = 0;
        Ans = No1 / No2;
        return Ans;
    }
}

class ThrowsDemo
{
    public static void main(String A[])
    {
       Demo obj = new Demo();
       int Ret = 0;
       int a = 0, b = 0;
       
       Scanner iobj = new Scanner(System.in);

       System.out.println("Enter the first Value");
       a = iobj.nextInt();

       System.out.println("Enter the Second Value");
       b = iobj.nextInt();

       try
       {
           Ret = obj.Division(a,b);
       }
       catch(ArithmeticException aobj)
       {
        System.out.println("Inside Catch");
       }
       System.out.println("Division is"+Ret);
    }
}