// Veřejné demo: veškeré operace pouze v paměti prohlížeče.
// Nikdy neodesílá osobní údaje na server, žádné skutečné platby ani faktury.
export const isPublicDemo = import.meta.env.VITE_PUBLIC_DEMO === 'true';

export const demoCoffees = [
 { id: 1, name: 'Ethiopia Guji', price: 349, quantity: 99, type: 'Arabica · ovocná', roastery: 'Nordbeans', description: 'Jemná výběrová káva s tóny jahod a citrusů.', imageUrl: 'https://images.unsplash.com/photo-1447933601403-0c6688de566e?w=900', available: true },
 { id: 2, name: 'Brazil Espresso', price: 279, quantity: 99, type: 'Espresso', roastery: 'Nordbeans', description: 'Čokoláda, oříšky a příjemně sladký závěr.', imageUrl: 'https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=900', available: true },
 { id: 3, name: 'Colombia Huila', price: 329, quantity: 99, type: 'Arabica', roastery: 'Doubleshot', description: 'Vyvážená chuť karamelu a červeného ovoce.', imageUrl: 'https://images.unsplash.com/photo-1517256673644-36ad11246d21?w=900', available: true },
 { id: 4, name: 'Kenya AA', price: 389, quantity: 99, type: 'Arabica', roastery: 'Father’s Coffee', description: 'Šťavnatá káva s výraznou ovocnou chutí.', imageUrl: 'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=900', available: true },
 { id: 5, name: 'Decaf Colombia', price: 335, quantity: 99, type: 'Bez kofeinu', roastery: 'Candycane Coffee', description: 'Sladká káva bez kofeinu pro každý večer.', imageUrl: 'https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=900', available: true },
 { id: 6, name: 'House Blend', price: 289, quantity: 99, type: 'Espresso Blend', roastery: 'Doubleshot', description: 'Klasická espresso směs s plným tělem.', imageUrl: 'https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=900', available: true },
];

let nextId = 1000;
const orders = new Map();
export function createDemoOrder(payload) {
 const items = payload?.items;
 if (!Array.isArray(items) || !items.length) throw new Error('Košík je prázdný.');
 for (const item of items) {
  if (!demoCoffees.some(p => p.id === item.coffeeId && Number.isInteger(item.quantity) && item.quantity > 0 && item.quantity <= 99)) throw new Error('Neplatný produkt nebo množství.');
 }
 const orderId = ++nextId;
 const paymentToken = crypto.randomUUID();
 orders.set(orderId, { token: paymentToken, paid: false });
 return { orderId, paymentToken };
}
export function completePublicDemoPayment(orderId, token) {
 const order = orders.get(orderId);
 if (!order || order.token !== token) throw new Error('Testovací objednávka vypršela.');
 order.paid = true;
 return { status: 'COMPLETED', emailSent: false };
}
export function downloadDemoReceipt(orderId, token) {
 const order = orders.get(orderId);
 if (!order || order.token !== token || !order.paid) throw new Error('Nejdříve potvrďte demo platbu.');
 const content = `KÁVOVÝ SKLAD – DEMO POTVRZENÍ\nObjednávka #${orderId}\nStav: SIMULOVANÁ PLATBA\n\nNEJDE O FAKTURU ANI DAŇOVÝ DOKLAD.\nŽádné peníze nebyly převedeny.\n`;
 const url = URL.createObjectURL(new Blob([content], { type: 'text/plain;charset=utf-8' }));
 const link = document.createElement('a'); link.href = url; link.download = `demo-potvrzeni-${orderId}.txt`;
 document.body.appendChild(link); link.click(); link.remove();
 setTimeout(() => URL.revokeObjectURL(url), 1000);
}
