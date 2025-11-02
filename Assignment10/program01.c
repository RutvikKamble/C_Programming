// WAP which accept radius of circle from user and calculate its area
// Consider value of PI as 3.14(Area = PI * Radius * Radius)

#include<stdio.h>

float CircleArea(float iNo)
{
    float PI = 0.0f, iArea = 0.0f;

    PI = 3.14f;

    iArea = PI * iNo * iNo;

    return iArea;

}

int main()
{
    float fValue = 0.0f, fRet = 0.0f;

    printf("Enter the value : ");
    scanf("%f",&fValue);

    fRet = CircleArea(fValue);

    printf("Radious = %f",fRet);

    return 0;
}