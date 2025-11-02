// WAP which accept tempreature in Farenheitnad convert it into celsius.
// (1 celsius = (Fahrenheit - 32)*(5/9))

#include<stdio.h>

float FhtoCs(float iNo)
{
    float fCels = 0.0f, fNum1 = 0.0f, fNum2 = 0.0f;

    fNum1 = iNo - 32;
    fNum2 = 5.0f / 9.0f;
    fCels = fNum1 * fNum2;

    return fCels;
}

int main()
{
    float fValue = 0.0f, fRet = 0;
    
    printf("Enter the tempreture in Farenheit : ");
    scanf("%f",&fValue);

    fRet = FhtoCs(fValue);

    printf("Tempreture in Celsius : %f",fRet);

    return 0;
}