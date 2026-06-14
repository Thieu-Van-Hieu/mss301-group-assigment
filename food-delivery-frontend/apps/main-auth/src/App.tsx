import {Toaster} from "sonner";

export default function App() {
	return (
		<div className="min-h-screen bg-gray-50 text-gray-900 antialiased">
			<Toaster
				position="top-right"
				richColors
				expand={true}
				closeButton={true}
				duration={2000}
			/>
		</div>
	);
}