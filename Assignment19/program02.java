// WAP to display grade of student based on mark

class logic
{
    void CheckGrade(int iNo)
    {
        if(iNo >= 80)
        {
            System.out.println("The Grade is : 'A'");
        }
        else if(iNo >= 60)
        {
            System.out.println("The Grade is : 'B'");
        }
        else if(iNo >= 40)
        {
            System.out.println("The Grade is : 'C'");
        }
        else
        {
            System.out.println("Fail");
        }
    }
}

class program02
{
    public static void main(String[] args)
    {
        logic obj = new logic();
        obj.CheckGrade(32);

    }
}
       