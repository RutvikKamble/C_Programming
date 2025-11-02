// WAP which accept range from user and display all even numbers in between that range.

#include<stdio.h>

void EvenRange(int iNo1, int iNo2)
{
     int iCnt = 0;

    if(iNo1 <= iNo2)
    {
        for(iCnt = iNo1; iCnt <= iNo2; iCnt++)
        {
            if(iCnt%2 == 0)
            {
                printf("%d\t",iCnt);
            }
        }
    }
    else
    {
        for(iCnt = iNo2; iCnt <= iNo1; iCnt++)
        {
             if(iCnt%2 == 0)
            {
                printf("%d\t",iCnt);
            }
        }
    }
}

int main()
{   
    int iValue1 = 0, iValue2 = 0;

    printf("Eneter the Range : ");
    scanf("%d %d",&iValue1,&iValue2);

    EvenRange(iValue1,iValue2);

    return 0;
}