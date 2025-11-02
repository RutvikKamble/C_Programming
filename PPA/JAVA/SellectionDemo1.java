import java.util.Scanner;

class SellectionDemo1
{
    public static void main(String A[])
    {
        Scanner sobj = new Scanner(System.in);

        int iNo = 0;

        System.out.println("Enter number : ");
        iNo = sobj.nextInt();

        if (iNo >= 18)
        {
            System.out.println("You can Vote");
        }
        else
        {
            System.out.println("You cant Vote");
        }

    }

}