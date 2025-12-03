// WAP which accept string from user and
// return difference between frequency of small character and frequency of Capital characters.

#include<stdio.h>

int CountCapital(char Str[])
{    
    int iCount1 = 0, iCount2 = 0, iDiff = 0;

    while(*Str != '\0')
    {
        if(*Str >= 'a' && *Str <= 'z')
        {
            iCount1++;
        }
        else if (*Str >= 'A' && *Str <= 'Z')
        {
            iCount2++;
        }
        Str++;
    }

    iDiff = iCount1 - iCount2;
    if(iDiff < 0)
    {
        iDiff = -iDiff;
    }

    return iDiff;
}

int main()
{
    int iRet = 0;
    char Arr[30] = {"\0"};
    
    printf("Enter the string : \n");
    scanf("%[^'\n']s",Arr);

    iRet = CountCapital(Arr);

    printf("The total Capital letters are : %d",iRet);
   
    return 0;
}