// WAP which accept range from user and return addition of all numbers in between
// that range. (Range should be contain positive number only)

#include<stdio.h>

int RengeSum(int iNo1, int iNo2)
{
     int iCnt = 0, iCount = 0;
     

     if(iNo1 < 0)
     {
        iNo1 = -iNo1;
     }
     if(iNo2 < 0)
     {
        iNo1 = -iNo1;
     }

    if(iNo1 <= iNo2)
    {
        for(iCnt = iNo1; iCnt <= iNo2; iCnt++)
        {
            iCount = iCount + iCnt;
        }
    }
    else
    {
        for(iCnt = iNo2; iCnt <= iNo1; iCnt++)
        {
            iCount = iCount + iCnt;
        }
    }
    return iCount;
}

int main()
{   
    int iValue1 = 0, iValue2 = 0, iSum = 0;

    printf("Eneter the Range : ");
    scanf("%d %d",&iValue1,&iValue2);

    iSum = RengeSum(iValue1,iValue2);

    printf("Addition of the Range : %d", iSum);

    return 0;
}