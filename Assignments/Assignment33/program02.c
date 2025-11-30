// Accept the character from user and check whether it is Capital or not(A ot Z)

#include<stdio.h>
#include<stdbool.h>

#define TRUE 1
#define FALSE 0

typedef int BOOL;

BOOL CheckAlphabet(char Char)
{
    if((Char >= 'A' && Char <= 'Z'))
    {
        return TRUE;
    }
    else
    {
        return FALSE;
    }
}

int main()
{
    char cValue = '\0';
    BOOL bRet = FALSE;

    printf("Enter the character : ");
    scanf("%c",&cValue);

    bRet = CheckAlphabet(cValue);

    if(bRet == TRUE)
    {
        printf("Yes, The alphabet is Capital : ");
    }
    else
    {
        printf("No, The alphabet is not Capitalr : ");
    }

    return 0;
}