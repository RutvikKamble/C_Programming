// Write a program which accept number from user and dispaly its multiplication of factor

#include<stdio.h>

int MultFact(int iNo)
{
    int iCnt = 0;
    int iMult = 0;

    for(iCnt = 1,iMult = 1; iCnt <= iNo/2; iCnt++)
    {
        if(iNo%iCnt == 0)
        {
            iMult = iMult*iCnt;
        }
    }

    return iMult;
}


int main()
{
    int iValue = 0;
    int iRet = 0;

    printf("Enter the Number : \n");
    scanf("%d\n",&iValue);

    iRet = MultFact(iValue);
    
    printf("Multiplication of Factors :%d",&iRet);

    return 0;
}