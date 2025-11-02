// WAP whether the given year is leap year or not

class logic
{
    void CheckLeapYear(int iNo)
    {
        if(iNo % 4 == 0)
        {
            System.out.println("Yes, it is leap year");
        }
        else
        {
            System.out.println("No, it is not leap year");
        }
    }
}

class program01
{
    public static void main(String[] args)
    {
        logic obj = new logic();
        obj.CheckLeapYear(2010);
        
    }
}