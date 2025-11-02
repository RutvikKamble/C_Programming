// Check Positive Negative or Zeor

#include<stdio.h>

int CheckLarger(int iNo1, int iNo2, int iNo3)
{
    if(iNo1 > iNo2 && iNo1 > iNo3)
    {
        printf("First number is larger");
        return iNo1;
    }
    else if (iNo2 > iNo3)
    {
        printf("Second number is larger");
        return iNo2;
    }
    else
    {
        printf("Third number is larger");
        return iNo3;
    }
}

int main()
{
    int iValue1 = 0, iValue2 = 0, iValue3 = 0, Result;

    printf("Enter Three Numbers : ");
    scanf("%d %d %d",&iValue1, &iValue2, &iValue3);

    Result = CheckLarger(iValue1,iValue2,iValue3);

    printf("%d",Result);

    return 0;
}