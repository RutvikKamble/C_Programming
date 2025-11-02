import java.util.Scanner;

class Dynamic
{
    public static void main(String A[])
    {
        Scanner sobj = new Scanner(System.in);
        int Arr[];

        int length = 0;

        System.out.println("Enter the number :");
        length = sobj.nextInt();

        Arr = new int[length];

        if(Arr == null)
        {
            System.out.println("Unable to allocate the memory");
        }
        else
        {
            System.out.println("Memory gets successfully allocated");
        }

        Arr = null;
        System.gc();
    }


}