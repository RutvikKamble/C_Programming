// WAP to calculate power of number using loob

class logic
{
    void CalculatePower(int iNo1, int iNo2)
    {
        int iCnt = 0;
        for(iCnt = 1; iCnt < iNo2; iCnt++)
        {
            iNo1 = iNo1 * iCnt;
        }
        System.out.println(iNo1);
    }
}

class porgram05
{
    logic obj = new logic();
    obj.CalculatePower(4,2);


}
