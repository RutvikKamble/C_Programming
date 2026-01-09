// Write generic program to accept n values and reverse the content

#include<iostream>
using namespace std;

template<class T>
void Display(T *Arr, int iNo)
{
    int iCnt = 0;

    for(iCnt = 0; iCnt < iNo; iCnt++)
    {
        cout<<Arr[iCnt];
        cout<<"\t";
    }
    cout<<"\n";
}

template<class T>
void Reverse(T *Arr, int iSize)
{
    T *Brr = nullptr;
    int iCnt = 0, iValue = 0;

    Brr = new T[iSize];

    for(iCnt = 0, iValue = 1; iCnt < iSize; iCnt++)
    {
        Brr[iCnt] = Arr[iSize-iValue];
        iValue++;
    }

    Display(Brr,iSize);

    delete []Brr;
}

int main()
{
    int *Arr = nullptr;
    
    int iCnt = 0, iSize = 0, iValue = 0;
    int iRet = 0;

    cout<<"Enter the number how many value that you print : ";
    cin>>iSize;

    Arr = new int[iSize];

    for(iCnt = 0; iCnt < iSize; iCnt++)
    {
        cout<<"Enter number : ";
        cin>>Arr[iCnt];
    }

    Display(Arr, iSize);

    Reverse(Arr, iSize);


    delete []Arr;

    return 0;
}