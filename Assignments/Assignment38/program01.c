// WAP whic accept string from user and 
// copy that character of string into another string in reverse order.

#include<stdio.h>

void StrCpyRev(char *src, char *dest)
{
    char *temp = src;

    while(*src != '\0')
    {
        src++;
    }
    src--;

    while(src >= temp)
    {
        *dest = *src;

        src--;
        dest++;
    }
    *dest = '\0';
}

int main()
{
    char Arr[50] = {('\0')};
    char Brr[50] = {('\0')};

    printf("Enter string : \n");
    scanf("%[^'\n']s",Arr);

    StrCpyRev(Arr,Brr);

    printf("Updated string is %s",Brr);

    return 0;
}


/*

#include<stdio.h>

void StrCpyRev(char *src, char *dest)
{
    char *temp = src;

    while (*src != '\0')
    {
        src++;
    }

    src--;   // Move back from '\0' to last character

    while (src >= temp)
    {
        *dest = *src;
        dest++;
        src--;
    }

    *dest = '\0';
}

int main()
{
    char Arr[50];
    char Brr[50];

    printf("Enter string : \n");
    scanf("%[^\n]", Arr);

    StrCpyRev(Arr, Brr);

    printf("Reversed string : %s\n", Brr);

    return 0;
}

*/