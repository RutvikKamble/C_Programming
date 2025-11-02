// WAP which accept range from user and return addition of all even number in between
// tha range. (Range should contains positive number only)

#include<stdio.h>

int RengeEvenSum(int iNo1, int iNo2)
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
            if(iCnt%2 == 0)
            {
                iCount = iCount + iCnt;
            }
        }
        return iCount;
    }
    else
    {
        for(iCnt = iNo2; iCnt <= iNo1; iCnt++)
        {
             if(iCnt%2 == 0)
            {
                iCount = iCount + iCnt;
            }
        }
        return iCount;
    }
}

int main()
{   
    int iValue1 = 0, iValue2 = 0, iSum = 0;

    printf("Eneter the Range : ");
    scanf("%d %d",&iValue1,&iValue2);

    iSum = RengeEvenSum(iValue1,iValue2);

    printf("Sum of Even number btn Range : %d",iSum);

    return 0;
}