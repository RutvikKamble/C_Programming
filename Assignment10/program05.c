// WAP which accept area in Square feet nad convert into square meter.
// (i Square feet = 0.0929 Square meter)

#include<stdio.h>

float SquareMeter(float iNo)
{
    float iNum1 = 0.0, iNum2 = 0.0;

    iNum1 = 0.0929;
    iNum2 = iNo * iNum1;

    return iNum2;
}

int main()
{
    float iValue = 0.0;
    double dRet = 0.0;

    printf("Enter Area in Square Feet : ");
    scanf("%f",&iValue);

    dRet = SquareMeter(iValue);

    printf("Square Meter : %f",dRet);

    return 0;
}