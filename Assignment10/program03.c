// WAP which accept distance in kilometr and convert it into meter.
// 1 kilometer = 1000 meter

#include<stdio.h>

float KMtoMeter(float iNo)
{
    float Num = 0.0f;

    Num = 1000;
    Num = Num * iNo;

    return Num;
}

int main()
{
    float iValue = 0.0f, iRet = 0.0f;

    printf("Enter the kilometer :");
    scanf("%f",&iValue);

    iRet = KMtoMeter(iValue);

    printf("Total Meter is : %f",iRet);

    return 0;
}