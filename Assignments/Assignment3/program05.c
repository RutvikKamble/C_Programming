// Accept on character from user and check whether that character is vowel (a,e,i,o,u) or not

#include<stdio.h>
#include<stdbool.h>

bool ChkVowel(char ch)
{
    if(ch == 'a' || 'e' || 'i' || 'o' || 'u')
    {
        return true;
    }
    else
    {
        return false;
    }
}


int main()
{
    char cValue = '\0';
    bool bRet = false;

    printf("Enter the character ; \n");

    scanf("%C\n",cValue);

    bRet = (ChkVowel(cValue));

    if(bRet == true)
    {
        printf("Yes, it is Vowel");
    }
    else
    {
        printf("No, it is not Vowel");
    }

    return 0;
} 